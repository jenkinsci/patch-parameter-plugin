package org.jenkinsci.plugins.patch;

import com.cloudbees.diff.ContextualPatch;
import com.cloudbees.diff.ContextualPatch.PatchReport;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import static java.lang.System.lineSeparator;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class SomeTest {    
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    /**
     * Verify patch file created from Windows with LF line endings.
     * @throws Exception 
     */    
    @Test
    public void testPatchFromUnix() throws Exception {
        verifyPatch("unix");
    }
    
    /**
     * Verify patch file created from Windows with CRLF line endings.
     * @throws Exception 
     */
    @Test
    public void testPatchFromWindows() throws Exception {
        verifyPatch("windows");
    }    
    
    private void verifyPatch(final String osPrefix) throws Exception {
        File dir = temp.newFolder();

        File foo = new File(dir, "Foo.txt");
        FileUtils.writeStringToFile(foo,
            String.format("aaa%1$sbbb%1$sccc%1$s", lineSeparator()));

        File diff = new File(dir, "diff.txt");
        FileUtils.copyURLToFile(getClass().getResource(osPrefix+"-gitstyle.patch"), diff);

        ContextualPatch patch = ContextualPatch.create(diff,dir);
        List<PatchReport> reports = patch.patch(false);
        for (PatchReport r : reports) {
            if (r.getFailure()!=null)
                throw new IOException("Failed to patch " + r.getFile(), r.getFailure());
        }

        Assert.assertEquals(String.format("aaa%1$sbbb2%1$sccc%1$s", lineSeparator()),
            FileUtils.readFileToString(foo));        
    }
}
