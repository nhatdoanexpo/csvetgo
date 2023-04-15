package com.bot.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EmailProfile {
 private String emailParrent;
 private String lastUpdate;
 private String linkProfile;
 private String email;
 private boolean deleted;
}
