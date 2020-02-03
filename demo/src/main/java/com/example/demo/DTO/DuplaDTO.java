package com.example.demo.DTO;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class DuplaDTO {

    private String membro1;
    private String membro2;

    public static DuplaDTO from(String dupla, List<String> membros) {

        String membro1 = membros.stream().filter(membro -> membro.split("\\|")[0].equals(dupla.split("\\|")[0])).findFirst().get();
        String membro2 = dupla.split("\\|").length == 2
                ? membros.stream().filter(membro -> membro.split("\\|")[0].equals(dupla.split("\\|")[1])).findFirst().get()
                : "";

        DuplaDTO duplaDTO = new DuplaDTO();
        duplaDTO.setMembro1(membro1.split("\\|")[1]);
        duplaDTO.setMembro2(StringUtils.isEmpty(membro2) ? "" : membro2.split("\\|")[1]);

        return duplaDTO;
    }

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
