package com.rogers.remip;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

@Xml(prefix = "jolt")
@Extension(name = "Jolt")
@Configurations(JoltConfiguration.class)
public class JoltExtension {

}
