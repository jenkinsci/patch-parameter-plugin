package org.jenkinsci.plugins.patch;

import hudson.Extension;
import hudson.MarkupText;
import hudson.console.ConsoleAnnotationDescriptor;
import hudson.console.ConsoleAnnotator;
import hudson.console.ConsoleNote;
import hudson.model.AbstractBuild;
import hudson.model.ParametersAction;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kohsuke Kawaguchi
 */
public class PatchNote extends ConsoleNote {
    private final int length;

    public PatchNote(int length) {
        this.length = length;
    }

    @Override
    public ConsoleAnnotator annotate(Object context, MarkupText text, int charPos) {
        if (context instanceof AbstractBuild) {
            AbstractBuild<?,?> b = (AbstractBuild) context;
            ParametersAction pa = b.getAction(ParametersAction.class);
            if (pa!=null) {
                PatchParameterValue p = (PatchParameterValue) pa.getParameter("patch.diff");
                if (p!=null) {
                    text.addHyperlink(charPos,charPos+length,"../parameter/patch.diff/"+p.getOriginalFileName());
                }
            }
        }
        return null;
    }

    public static String encodeTo(String text) {
        try {
            return new PatchNote(text.length()).encode()+text;
        } catch (IOException e) {
            // impossible, but don't make this a fatal problem
            LOGGER.log(Level.WARNING, "Failed to serialize "+PatchNote.class,e);
            return text;
        }
    }

    @Extension
    public static final class DescriptorImpl extends ConsoleAnnotationDescriptor {
        public String getDisplayName() {
            return "Link to patch.diff";
        }
    }

    private static final Logger LOGGER = Logger.getLogger(PatchNote.class.getName());
}
