package com.eman.authenticationcrud.Model;

public class Book {

    private int id;
    private String name , author , created_at , updated_at ,url,image;

    public Book() {
    }

    public Book(int id, String name, String author, String created_at, String updated_at, String url, String image) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.url = url;
        this.image = image;
    }

    public Book(int id, String name, String author, String created_at, String updated_at, String url) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.url = url;
    }

    public Book(int id, String name, String author, String created_at, String updated_at) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.created_at = created_at;
        this.updated_at = updated_at;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Book(String name, String author) {

        this.name = name;
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
