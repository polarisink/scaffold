package com.scaffold.rbac.components;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import static org.springframework.aot.hint.MemberCategory.DECLARED_FIELDS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_METHODS;

/** Native Image resources required by the RBAC data initializer. */
public class RbacRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("rbac/rbac-seed-data.json");
        for (String nestedType : new String[]{"RbacSeedData", "OrgSeed", "RoleSeed", "UserSeed", "MenuSeed"}) {
            hints.reflection().registerType(
                    TypeReference.of(RbacDataInitializer.class.getName() + "$" + nestedType),
                    INVOKE_DECLARED_CONSTRUCTORS,
                    INVOKE_DECLARED_METHODS,
                    DECLARED_FIELDS);
        }
    }
}
