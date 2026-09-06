package com.manpowergroup.blog.module.member.application.service.member;

import com.manpowergroup.blog.module.member.application.command.member.MemberCreateCommand;
import com.manpowergroup.blog.module.member.application.command.member.MemberUpdateCommand;

public interface MemberAppService {

    Long create(MemberCreateCommand command);

    void updateProfile(MemberUpdateCommand command);

}
