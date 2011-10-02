package com.unitt.buildtools.servicemanager;

import java.util.List;

public interface CodeGenerator<T extends ClassConfig>
{
    public List<CodeOutput> generateCode(T aConfig);
}
