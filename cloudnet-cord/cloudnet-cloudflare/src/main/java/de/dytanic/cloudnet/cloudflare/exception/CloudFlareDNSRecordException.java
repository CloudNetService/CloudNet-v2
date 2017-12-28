/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare.exception;

/**
 * Created by Tareko on 26.08.2017.
 */
public class CloudFlareDNSRecordException extends RuntimeException {

    public CloudFlareDNSRecordException(String message)
    {
        super(message);
    }
}