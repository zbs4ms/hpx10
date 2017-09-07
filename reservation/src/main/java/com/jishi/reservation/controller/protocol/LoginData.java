package com.jishi.reservation.controller.protocol;

import lombok.Data;

/**
 * Created by sloan on 2017/9/7.
 */

@Data
public class LoginData {

    private String token;

    private String nickname;
    private String headPortrait;
    private String telephone;
}
