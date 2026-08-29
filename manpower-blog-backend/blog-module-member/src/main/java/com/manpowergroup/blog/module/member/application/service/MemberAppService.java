package com.manpowergroup.blog.module.member.application.service;

import com.manpowergroup.blog.module.member.application.command.MemberCreateCommand;
import com.manpowergroup.blog.module.member.application.command.MemberUpdateCommand;

public interface MemberAppService {

    Long create(MemberCreateCommand command);

    void updateProfile(MemberUpdateCommand command);

}
