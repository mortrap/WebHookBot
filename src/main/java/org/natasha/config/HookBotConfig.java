package org.natasha.config;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HookBotConfig {
    private String name;
    private String token;
    private Long adminId;
}
