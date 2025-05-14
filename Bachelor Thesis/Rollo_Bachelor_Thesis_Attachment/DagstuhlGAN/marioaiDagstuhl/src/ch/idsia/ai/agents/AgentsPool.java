package ch.idsia.ai.agents;

import wox.serial.Easy;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: May 9, 2009
 * Time: 8:28:06 PM
 * Package: ch.idsia.ai.agents
 */

public final class AgentsPool
{
    private static IAgent currentAgent = null;

    public static void addAgent(IAgent agent) {
        agentsHashMap.put(agent.getName(), agent);
    }

    public static void addAgent(String agentWOXName) throws IllegalFormatException
    {
        addAgent(load(agentWOXName));
    }

    public static IAgent load (String name) {
        IAgent agent;
        try {
            agent = (IAgent) Class.forName(name).getDeclaredConstructor().newInstance();
        }
        catch (ClassNotFoundException e) {
            System.out.println (name + " is not a class name; trying to load a wox definition with that name.");
            agent = (IAgent) Easy.load (name);
        }
        catch (Exception e) {
            e.printStackTrace ();
            agent = null;
            System.exit (1);
        }
        return agent;
    }

    public static Collection<IAgent> getAgentsCollection()
    {
        return agentsHashMap.values();
    }

    public static Set<String> getAgentsNames()
    {
        return AgentsPool.agentsHashMap.keySet();
    }

    public static IAgent getAgentByName(String agentName)
    {
        // There is only one case possible;
        IAgent ret = AgentsPool.agentsHashMap.get(agentName);
        if (ret == null)
            ret = AgentsPool.agentsHashMap.get(agentName.split(":")[0]);
        return ret;
    }

    public static IAgent getCurrentAgent()
    {
        if (currentAgent == null)
            currentAgent = (IAgent) getAgentsCollection().toArray()[0];
        return currentAgent;
    }

    public static void setCurrentAgent(IAgent agent) {
        currentAgent = agent;
    }

    public static void setCurrentAgent(String agentWOXName)
    {
        setCurrentAgent(AgentsPool.load(agentWOXName));
    }

    static HashMap<String, IAgent> agentsHashMap = new LinkedHashMap<String, IAgent>();
}
