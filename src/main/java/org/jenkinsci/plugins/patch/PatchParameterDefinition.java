package org.jenkinsci.plugins.patch;

import hudson.Extension;
import hudson.cli.CLICommand;
import hudson.model.FileParameterDefinition;
import hudson.model.FileParameterValue;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author Kohsuke Kawaguchi
 */
public class PatchParameterDefinition extends FileParameterDefinition {
    @DataBoundConstructor
    public PatchParameterDefinition() {
        super(LOCATION,"");
    }

    @Override
    public String getDescription() {
        return Messages.PatchParameterDefinition_Description();
    }

    @Override
    public PatchParameterValue createValue(CLICommand command, String value) throws IOException, InterruptedException {
        return wrap((FileParameterValue) super.createValue(command, value));
    }

    @Override
    public PatchParameterValue createValue(StaplerRequest req) {
        return wrap((FileParameterValue) super.createValue(req));
    }

    @Override
    public PatchParameterValue createValue(StaplerRequest req, JSONObject jo) {
        return wrap(super.createValue(req, jo));
    }

    private PatchParameterValue wrap(FileParameterValue rhs) {
        try {
            // TODO: once we bump up to 1.486 or so, there should be a getter for this
            Field $file = FileParameterValue.class.getDeclaredField("file");
            $file.setAccessible(true);

            Field $location = FileParameterValue.class.getDeclaredField("location");
            $location.setAccessible(true);

            PatchParameterValue v = new PatchParameterValue(rhs.getName(), (FileItem) $file.get(rhs));
            $location.set(v,LOCATION);
            return v;
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
    }

    @Extension
    public static class DescriptorImpl extends FileParameterDefinition.DescriptorImpl {
        @Override
        public String getDisplayName() {
            return "Patch file as a parameter";
        }
    }

    static final String LOCATION = "patchDiff";
}
