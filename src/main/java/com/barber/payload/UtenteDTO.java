package com.barber.payload;

import com.barber.enumeration.ERuolo;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UtenteDTO {

    @NotBlank(message = "⚠️ Il campo ' nome ' è obbligatorio! ⚠️")
    private String nome;

    @NotBlank(message = "⚠️ Il campo ' cognome ' è obbligatorio! ⚠️")
    private String cognome;

    @NotBlank(message = "⚠️ Il campo ' email ' è obbligatorio! ⚠️")
    @Email(message = "❌ Email non valida! ❌")
    private String email;


    @NotBlank(message = "⚠️ Il campo ' username ' è obbligatorio! ⚠️")
    @Size(min = 6, max = 20)
    private String username;


    @NotBlank(message = "⚠️ Il campo ' password ' è obbligatorio! ⚠️")
    @Size(min = 6, max = 20)
    private String password;

    private String avatar;

    private String tipoRuolo;
}
