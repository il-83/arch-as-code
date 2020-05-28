package net.trilogy.arch.validation;

import com.google.common.collect.ImmutableList;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureReader;
import net.trilogy.arch.schema.ArchitectureDataStructureSchemaValidator;

public abstract class ArchitectureDataStructureValidatorFactory {

    public static ArchitectureDataStructureValidator create() {
        return new ArchitectureDataStructureValidator(
                ImmutableList.of(
                        new ModelValidator()
                ),
                new ArchitectureDataStructureSchemaValidator(),
                new ArchitectureDataStructureReader(new FilesFacade())
        );
    }
}
