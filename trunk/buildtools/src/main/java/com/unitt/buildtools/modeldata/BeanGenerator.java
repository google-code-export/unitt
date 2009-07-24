package com.unitt.buildtools.modeldata;

public class BeanGenerator
{
    public StringBuffer process(ModelDataConfig aConfig)
    {
        StringBuffer out = new StringBuffer();
        
        out.append("package " + aConfig.getGeneratedPackageName() + ";\n");
        out.append("\n");
        out.append("\n");
        out.append("import java.util.ArrayList;\n");
        out.append("import java.util.Collection;\n");
        out.append("import java.util.HashMap;\n");
        out.append("import java.util.Map;\n");
        out.append("\n");
        out.append("import " + aConfig.getPackageName() + "." + aConfig.getClassName() + ";\n");
        out.append("\n");
        out.append("\n");
        if (aConfig.isSerializable())
        {
            out.append("@SuppressWarnings(\"serial\")\n");
        }
        out.append("public class " + aConfig.getGeneratedClassName() + " extends " + aConfig.getClassName() + "\n");
        out.append("{\n");
        out.append("\t@SuppressWarnings(\"unchecked\")\n");
        out.append("\tpublic <X> X get( String aProperty )\n");
        out.append("\t{\n");
        out.append("\t\tif ( aProperty != null )\n");
        out.append("\t\t{\n");
        out.append("\t\t\t//try to return value\n");
        boolean isFirst = true;
        for (PropertyInfo property : aConfig.getProperties().values())
        {
            if (property.hasGetter())
            {
                fillGetter(out, property, isFirst);
                isFirst = false;
            }
        }
        out.append("\t\t}\n");
        out.append("\t\t//could not find value - just return null\n");
        out.append("\t\treturn null;\n");
        out.append("\t}\n");
        out.append("\n");
        out.append("\tpublic Map<String, Object> getProperties()\n");
        out.append("\t{\n");
        out.append("\t\tMap<String, Object> properties = new HashMap<String, Object>();\n");
        out.append("\t\tfor ( String property : getPropertyNames() )\n");
        out.append("\t\t{\n");
        out.append("\t\t\tproperties.put( property, get( property ) );\n");
        out.append("\t\t}\n");
        out.append("\t\treturn properties;\n");  
        out.append("\t}\n");
        out.append("\n");
        out.append("\tpublic Collection<String> getPropertyNames()\n");
        out.append("\t{\n");
        out.append("\t\tCollection<String> names = new ArrayList<String>();\n");
        for (PropertyInfo property : aConfig.getProperties().values())
        {
            if (property.hasGetter())
            {
               out.append("\t\tnames.add( \"" + property.getId() + "\" );\n");
            }
        }
        out.append("\t\treturn names;\n");
        out.append("\t}\n");
        out.append("\n");
        out.append("@SuppressWarnings(\"unchecked\")\n");
        out.append("\tpublic <X> X remove( String aProperty )\n");
        out.append("\t{\n");
        out.append("\t\treturn (X) set(aProperty, null);\n");
        out.append("\t}\n");
        out.append("\n");
        out.append("\t@SuppressWarnings(\"unchecked\")\n");
        out.append("\tpublic <X> X set( String aProperty, X aValue )\n");
        out.append("\t{\n");
        out.append("\t\tif (aProperty != null)\n");
        out.append("\t\t{\n");
        isFirst = true;
        for (PropertyInfo property : aConfig.getProperties().values())
        {
            if (property.hasSetter())
            {
                fillSetter(out, property, isFirst);
                isFirst = false;
            }
        }
        out.append("\t\t}\n");
        out.append("\t\tthrow new UnsupportedOperationException( \"This model is read only.\" );\n");
        out.append("\t}\n");
        out.append("}\n");
        
        return out;
    }

    protected void fillGetter(StringBuffer aOut, PropertyInfo aProperty, boolean aIsFirst)
    {
        String ifClause = "else if";
        if (aIsFirst)
        {
            ifClause = "if";
        }
        aOut.append("\t\t\t" + ifClause + " ( \"" + aProperty.getId() + "\".equals( aProperty ) )\n");
        aOut.append("\t\t\t{\n");
        if (aProperty.isPrimitive())
        {
            aOut.append("\t\t\t\treturn ( X ) ((" + aProperty.getGetterType()  + ") " + aProperty.getGetterPrefix() + aProperty.getName() + "());\n");
        }
        else
        {
            aOut.append("\t\t\t\treturn ( X ) " + aProperty.getGetterPrefix() + aProperty.getName() + "();\n");
        }
        aOut.append("\t\t\t}\n");
    }

    protected void fillSetter(StringBuffer aOut, PropertyInfo aProperty, boolean aIsFirst)
    {
        String ifClause = "else if";
        if (aIsFirst)
        {
            ifClause = "if";
        }
        aOut.append("\t\t\t" + ifClause + " ( \"" + aProperty.getId() + "\".equals( aProperty ) )\n");
        aOut.append("\t\t\t{\n");
        aOut.append("\t\t\t\tX old = (X) get(aProperty);\n");

        boolean innerIsFirst = true;     
        String innerIfClause = "else if";   
        for (String setter : aProperty.getSetters())
        {
            innerIfClause = "else if";
            if (innerIsFirst)
            {
                innerIfClause = "if";
                innerIsFirst = false;
            }
            aOut.append("\t\t\t\t" + innerIfClause + " (aValue instanceof " + setter + ")\n");
            aOut.append("\t\t\t\t{\n");
            if (aProperty.isPrimitive())
            {
                aOut.append("\t\t\t\t\tif (aValue != null)\n");
                aOut.append("\t\t\t\t\t{\n");
                aOut.append("\t\t\t\t\t\tset" + aProperty.getName() + "((" + setter + ") aValue);\n");
                aOut.append("\t\t\t\t\t}\n");
            }
            else
            {
                aOut.append("\t\t\t\t\tset" + aProperty.getName() + "((" + setter + ") aValue);\n");
            }
            aOut.append("\t\t\t\t}\n");
        }
        aOut.append("\t\t\t\treturn old;\n");
        aOut.append("\t\t\t}\n");
    }
}
