package com.api.devsync.model.viewmodel.fromWebhook;

import lombok.Data;

@Data
public class Committer {
    private String name;
    private String email;
    private String username;
}
