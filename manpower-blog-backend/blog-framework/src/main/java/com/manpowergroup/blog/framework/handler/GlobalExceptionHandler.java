package com.manpowergroup.blog.framework.handler;

import com.manpowergroup.blog.shared.api.Result;
import com.manpowergroup.blog.shared.dto.ValidationErrors;
import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.exception.BizException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Locale;

/**
 * 全体共通の例外ハンドラ
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /* ====================== 共通ユーティリティ ====================== */

    /**
     * 内部例外の詳細は言語や実装情報を含むため、API利用者には返さない
     */
    private String safeDetail(String detail) {
        return null;
    }

    /**
     * 現在の Locale に基づいてメッセージコードを変換する
     */
    private String i18n(String codeOrRaw, Object... args) {
        if (codeOrRaw == null || codeOrRaw.isBlank()) return "";
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(codeOrRaw, args, locale);
        } catch (NoSuchMessageException ignore) {
            return codeOrRaw;
        }
    }

    /**
     * 共通エラーログ出力（traceId 付き）
     */
    private void logError(String message, String detail, Throwable e) {
        String traceId = MDC.get("traceId");
        if (e != null) {
            log.error("[traceId={}] {} | {}", traceId, message, detail, e);
        } else {
            log.error("[traceId={}] {} | {}", traceId, message, detail);
        }
    }

    /* ====================== 業務例外 ====================== */

    /**
     * 業務例外（BizException）のハンドリング。
     */
    @ExceptionHandler(BizException.class)
    public Result<Object> handleBiz(BizException e) {

        // 1) メッセージキー：BizException の messageKey を優先して使用（なければデフォルト）
        String key = (e.getMessageKey() == null || e.getMessageKey().isBlank())
                ? ErrorCode.BIZ_ERROR.message()
                : e.getMessageKey();

        // 2) エラーコード
        int code = (e.getCode() != null) ? e.getCode().code() : ErrorCode.BIZ_ERROR.code();

        // 3) フロント向けメッセージ（i18n対応、args対応）
        String msg = i18n(key, e.getArgs());

        // 4) 詳細情報：BizException の detail を優先（主にテスト環境で表示）
        String detail = (e.getDetail() != null && !e.getDetail().isBlank())
                ? e.getDetail()
                : null;

        logError(msg, detail, e);
        return Result.error(code, msg).withDetail(safeDetail(detail));
    }


    /* ====================== バリデーション例外 ====================== */

    /**
     * @Valid による入力チェックエラー
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<ValidationErrors> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        var items = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> ValidationErrors.ErrorItem.of(
                        fe.getField(),
                        fe.getDefaultMessage(),
                        fe.getField()
                ))
                .toList();

        String msg = i18n(ErrorCode.VALIDATION_ERROR.message());
        String detail = "入力検証エラー（" + items.size() + "件）";
        logError(msg, detail, e);

        return Result.of(ErrorCode.VALIDATION_ERROR.code(), msg, ValidationErrors.of(items))
                .withDetail(safeDetail(detail));
    }

    /**
     * @Validated による制約違反エラー
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<ValidationErrors> handleConstraintViolation(ConstraintViolationException e) {
        var items = e.getConstraintViolations().stream()
                .map(v -> {
                    String path = v.getPropertyPath().toString();
                    String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
                    return ValidationErrors.ErrorItem.of(field, v.getMessage(), field);
                })
                .toList();

        String msg = i18n(ErrorCode.VALIDATION_ERROR.message());
        String detail = "ConstraintViolation エラー（" + items.size() + "件）";
        logError(msg, detail, e);

        return Result.of(ErrorCode.VALIDATION_ERROR.code(), msg, ValidationErrors.of(items))
                .withDetail(safeDetail(detail));
    }

    /* ====================== Web 関連例外 ====================== */

    /**
     * 必須リクエストパラメータ不足
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<ValidationErrors> handleMissingParam(MissingServletRequestParameterException e) {
        String msg = i18n("error.missing_param", e.getParameterName());
        String detail = "必須パラメータ不足：" + e.getParameterName();
        logError(msg, detail, e);

        var item = ValidationErrors.ErrorItem.of(
                e.getParameterName(),
                "error.missing_param",
                e.getParameterName()
        );

        return Result.of(ErrorCode.BAD_REQUEST.code(), msg, ValidationErrors.of(java.util.List.of(item)))
                .withDetail(safeDetail(detail));
    }

    /**
     * DB UNIQUE制約違反（DuplicateKey）。
     *
     * <p>全ての UNIQUE 制約はアプリケーション層で事前チェック済みであるため、
     * 本ハンドラに到達するのは事前チェックをすり抜けた同時実行時のみである。
     * 制約名から個別メッセージへの変換は行わない。
     * DDL 側の制約名変更に追随できず、実在しない制約名が残存する事故が発生したため、
     * 各モジュールの DB 制約名は framework 層に持たない方針とする。
     * 違反した制約名は detail としてログにのみ出力する。</p>
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Object> handleDuplicateKey(DuplicateKeyException e) {
        final String detail = e.getMostSpecificCause().getMessage();
        final String msg = i18n(ErrorCode.CONFLICT.message());

        log.warn("[traceId={}] UNIQUE制約違反 | {}", MDC.get("traceId"), detail, e);

        return Result.error(ErrorCode.CONFLICT.code(), msg)
                .withDetail(safeDetail(detail));
    }

    /**
     * リクエストボディの解析失敗
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Object> handleNotReadable(HttpMessageNotReadableException e) {
        String msg = i18n(ErrorCode.BAD_REQUEST.message());
        String detail = e.getMessage();
        logError(msg, detail, e);
        return Result.error(ErrorCode.BAD_REQUEST.code(), msg).withDetail(safeDetail(detail));
    }

    /**
     * HTTP メソッド未対応
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Object> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        String msg = i18n(ErrorCode.METHOD_NOT_ALLOWED.message());
        String detail = e.getMessage();
        logError(msg, detail, e);
        return Result.error(ErrorCode.METHOD_NOT_ALLOWED.code(), msg).withDetail(safeDetail(detail));
    }

    /**
     * サポートされていない Content-Type
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result<Object> handleMediaType(HttpMediaTypeNotSupportedException e) {
        String msg = i18n(ErrorCode.UNSUPPORTED_MEDIA_TYPE.message());
        String detail = e.getMessage();
        logError(msg, detail, e);
        return Result.error(ErrorCode.UNSUPPORTED_MEDIA_TYPE.code(), msg).withDetail(safeDetail(detail));
    }

    /**
     * 権限不足エラー
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Object> handleAccessDenied(AccessDeniedException e) {
        String msg = i18n(ErrorCode.FORBIDDEN.message());
        String detail = e.getMessage();
        logError(msg, detail, e);
        return Result.error(ErrorCode.FORBIDDEN.code(), msg).withDetail(safeDetail(detail));
    }

    /**
     * リソース未存在
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Object> handleNoResourceFound(NoResourceFoundException e) {
        String msg = i18n(ErrorCode.NOT_FOUND.message());
        String detail = e.getMessage();
        logError(msg, detail, e);
        return Result.error(ErrorCode.NOT_FOUND.code(), msg).withDetail(safeDetail(detail));
    }

    /**
     * アップロードサイズ超過
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Object> handleMaxUpload(MaxUploadSizeExceededException e) {
        String msg = i18n("error.upload.too_large");
        String detail = e.getMessage();
        logError(msg, detail, e);
        return Result.error(413, msg).withDetail(safeDetail(detail));
    }




    /* ====================== その他例外 ====================== */

    /**
     * 想定外例外（HTTP 500）
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> handleOther(Exception e) {
        String msg = i18n(ErrorCode.SERVER_ERROR.message());
        String detail = e.getMessage();
        logError(msg, detail, e);
        return Result.error(ErrorCode.SERVER_ERROR.code(), msg).withDetail(safeDetail(detail));
    }
}
