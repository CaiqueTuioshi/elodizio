package com.example.demo.DTO;

import lombok.Data;

@Data
public class DuplaDTO {

    private String membro1;
    private String membro2;

    public String getMembro1() {
        return membro1;
    }

    public void setMembro1(String membro1) {
        this.membro1 = membro1;
    }

    public String getMembro2() {
        return membro2;
    }

    public void setMembro2(String membro2) {
        this.membro2 = membro2;
    }
}