package net.nahknarmi.arch.domain.c4;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipPair {
    @NonNull
    private String name;
    @NonNull
    private String action;
    @NonNull
    private String with;
    @NonNull
    private String description;
}
