package cz.agents.map4rt.agent;

import java.util.List;

import org.jgrapht.DirectedGraph;

import cz.agents.map4rt.CommonTime;
import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.probleminstance.Environment;
import tt.euclidtime3i.region.MovingCircle;
import tt.jointeuclid2ni.probleminstance.RelocationTask;
import tt.jointeuclid2ni.probleminstance.RelocationTaskImpl;

public class DFCFSAgent extends PlanningAgent {

	public DFCFSAgent(String name, Point start, List<RelocationTask> tasks,
			Environment env, DirectedGraph<Point, Line> planningGraph, 
			int agentBodyRadius, float maxSpeed, int maxTime, int timeStep) {
		super(name, start, tasks, env, planningGraph, agentBodyRadius, maxSpeed, maxTime, timeStep);
		
		handleNewTask(new RelocationTaskImpl(0, 0, start));
	}

	@Override
	protected void handleNewTask(RelocationTask task) {
		// nearest timestep in past
		int minTime = ((int) Math.floor( (double) CommonTime.currentTimeMs() / (double) timeStep) ) * timeStep;
		// start at a multiple of timestep
		int depTime = ((int) Math.ceil( (double) (CommonTime.currentTimeMs() + T_PLANNING) / (double) timeStep) ) * timeStep;
		
		EvaluatedTrajectory traj = getBestResponseTrajectory(
				getCurrentPos(), minTime, depTime, task.getDestination(),
				Token.getReservedRegions(getName()), maxTime);
		
		assert timeStep % 2 == 0;
		int samplingInterval = timeStep/2;
		Token.register(getName(), new MovingCircle(traj, agentBodyRadius, samplingInterval));
		currentTrajectory = traj;
	}

	@Override
	public void start() {}

}
