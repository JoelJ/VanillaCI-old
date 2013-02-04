package com.vanillaci.slave.job;

import com.google.common.collect.ImmutableSet;
import com.vanillaci.slave.exceptions.JobNotFoundException;
import com.vanillaci.slave.script.ScriptName;
import com.vanillaci.slave.script.ScriptRepository;
import com.vanillaci.slave.util.Confirm;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Joel Johnson
 * Date: 12/8/12
 * Time: 11:14 AM
 */
public class JobRepository {
	private final ScriptRepository scriptRepository;
	private final Map<String, Job> jobs;

	public JobRepository(ScriptRepository scriptRepository) {
		this.scriptRepository = Confirm.notNull("scriptRepository", scriptRepository);
		jobs = new ConcurrentHashMap<String, Job>();
	}

	/**
	 * Finds all the scripts that are missing in order to run the given job.
	 * @param job Job to check against.
	 * @return All the scripts that need to be deployed before executing this job.
	 */
	public Set<ScriptName> findMissingScriptsForJob(Job job) {
		Confirm.notNull("com/joelj/vanillaci/job", job);

		Set<ScriptName> requiredScripts = job.getRequiredScripts();
		ImmutableSet.Builder<ScriptName> missingScripts = ImmutableSet.builder();

		for (ScriptName script : requiredScripts) {
			boolean hasScript = scriptRepository.hasScript(script.getName(), script.getHash());
			if(!hasScript) {
				missingScripts.add(script);
			}
		}

		return missingScripts.build();
	}

	public void add(Job job) {
		Confirm.notNull("job", job);
		jobs.put(job.getName(), job);
	}

	public Job remove(String name) {
		Job remove = jobs.remove(name);
		if(remove == null) {
			throw new JobNotFoundException(name);
		}
		return remove;
	}
}
