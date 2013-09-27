// PathSBML Plugin
// SBML Plugin for PathVisio.
// Copyright 2013 developed for Google Summer of Code
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package org.pathvisio.sbml;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pathvisio.desktop.plugin.Plugin;

/**
 * This class activates the SBML Plugin
 * @author applecool
 *
 */
public class Activator implements BundleActivator
{
	private SBMLPlugin plugin;
	
	/**
	 * This method starts the SBML Plugin
	 * @exception Exception
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		plugin = new SBMLPlugin();
		context.registerService(Plugin.class.getName(), plugin, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		if (plugin != null) plugin.done();
	}

}
