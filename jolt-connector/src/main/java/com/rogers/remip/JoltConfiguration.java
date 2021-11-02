package com.rogers.remip;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

@Operations(JoltOperation.class)
@ConnectionProviders(JoltConnectionProvider.class)
public class JoltConfiguration {


}
