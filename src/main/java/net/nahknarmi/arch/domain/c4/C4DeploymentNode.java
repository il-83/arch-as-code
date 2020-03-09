package net.nahknarmi.arch.domain.c4;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4DeploymentNode extends BaseEntity implements Entity {
    private String technology;
    private String environment;
    private Integer instances;
    private List<C4DeploymentNode> children = new ArrayList<>();
    private List<C4ContainerInstance> containerInstances = new ArrayList<>();

    public C4Type getType() {
        return C4Type.deploymentNode;
    }
}