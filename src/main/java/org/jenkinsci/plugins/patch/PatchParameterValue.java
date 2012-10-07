package org.jenkinsci.plugins.patch;

import hudson.FilePath;
import hudson.Launcher;
import hudson.console.HyperlinkNote;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FileParameterValue;
import hudson.tasks.BuildWrapper;
import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
public class PatchParameterValue extends FileParameterValue {
    public PatchParameterValue(String name, File file, String originalFileName) {
        super(name, file, originalFileName);
    }

    @DataBoundConstructor
    public PatchParameterValue(String name, FileItem file) {
        super(name, file);
    }

    @Override
    public BuildWrapper createBuildWrapper(AbstractBuild<?, ?> build) {
        final BuildWrapper nested = super.createBuildWrapper(build);
        return new BuildWrapper() {
            @Override
            public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                FilePath patch = build.getWorkspace().child(PatchParameterDefinition.LOCATION);
                patch.delete();
                Environment env = nested.setUp(build,launcher,listener);
                if (patch.exists()) {
                    listener.getLogger().println("Applying "+ PatchNote.encodeTo("a patch"));
                    patch.act(new ApplyTask());
                }

                return env;
            }
        };
    }
}
