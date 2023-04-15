package com.bot.model;

import java.util.Map;
 

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostMessage {
    private String actionType; 
    private Map<String,Object> data;  
    private Status messageStatus;
    public static enum Status {
        NEW, DONE, FAIL
    }
}


