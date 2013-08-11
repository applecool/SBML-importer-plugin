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
