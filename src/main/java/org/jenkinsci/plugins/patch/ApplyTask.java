package org.jenkinsci.plugins.patch;

import com.cloudbees.diff.ContextualPatch;
import com.cloudbees.diff.ContextualPatch.PatchReport;
import com.cloudbees.diff.PatchException;
import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;
import hudson.util.IOException2;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Applies a patch.
 *
 * @author Kohsuke Kawaguchi
 */
class ApplyTask implements FileCallable<Void> {
    public Void invoke(File diff, VirtualChannel channel) throws IOException, InterruptedException {
        ContextualPatch patch = ContextualPatch.create(diff,diff.getParentFile());
        try {
            List<PatchReport> reports = patch.patch(false);
            for (PatchReport r : reports) {
                if (r.getFailure()!=null)
                    throw new IOException("Failed to patch " + r.getFile(), r.getFailure());
            }
        } catch (PatchException e) {
            throw new IOException2("Failed to apply the patch: "+diff,e);
        }

        return null;
    }

    private static final long serialVersionUID = 1L;
}
