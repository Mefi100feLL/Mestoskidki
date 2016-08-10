package com.popcorp.parser.mestoskidki.entity;

public class Error implements DomainObject {

    public static final String REPOSITORY = "errorRepository";

    private String subject;
    private String body;
    private String datetime;

    public Error(String subject, String body, String datetime) {
        this.subject = subject;
        this.body = body;
        this.datetime = datetime;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Error)) return false;
        Error error = (Error) object;
        return getBody().equals(error.getBody());
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDateTime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
