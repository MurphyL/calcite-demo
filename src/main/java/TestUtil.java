import org.apache.calcite.util.Sources;

/**
 * -
 *
 * @date 2020/11/17
 * @author: hao.luo <hao.luo@china.zhaogang.com>
 */
public class TestUtil {
    public static String resourcePath(String path) {
        return Sources.of(TestUtil.class.getResource("/" + path)).file().getAbsolutePath();
    }
}
