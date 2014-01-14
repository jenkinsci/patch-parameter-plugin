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
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class SomeTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void foo() throws Exception {
        File dir = temp.newFolder();

        File foo = new File(dir, "Foo.txt");
        FileUtils.writeStringToFile(foo,"aaa\nbbb\nccc\n");

        File diff = new File(dir, "diff.txt");
        FileUtils.copyURLToFile(getClass().getResource("gitstyle.patch"), diff);

        ContextualPatch patch = ContextualPatch.create(diff,dir);
        List<PatchReport> reports = patch.patch(false);
        for (PatchReport r : reports) {
            if (r.getFailure()!=null)
                throw new IOException("Failed to patch " + r.getFile(), r.getFailure());
        }

        Assert.assertEquals("aaa\nbbb2\nccc\n",FileUtils.readFileToString(foo));
    }
}
