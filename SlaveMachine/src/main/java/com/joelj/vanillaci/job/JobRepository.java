package com.joelj.vanillaci.job;

import com.google.common.collect.ImmutableSet;
import com.joelj.vanillaci.script.ScriptName;
import com.joelj.vanillaci.script.ScriptRepository;
import com.joelj.vanillaci.util.Confirm;

import java.util.Set;

/**
 * User: Joel Johnson
 * Date: 12/8/12
 * Time: 11:14 AM
 */
public class JobRepository {
	private final ScriptRepository scriptRepository;

	public JobRepository(ScriptRepository scriptRepository) {
		this.scriptRepository = Confirm.notNull("scriptRepository", scriptRepository);
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
}
