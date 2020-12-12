import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Parser {

    protected List<Bilidm.DanmakuElem> parse(List<FileInputStream> protoFileInList) {
        List<Bilidm.DanmakuElem> dmElemList = new LinkedList<>();

        for (FileInputStream protoFileIn : protoFileInList) {
            try {
                Bilidm.DmSegMobileReply bilidm = Bilidm.DmSegMobileReply.parseFrom(protoFileIn);
                dmElemList.addAll(bilidm.getElemsList());
                protoFileIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dmElemList;
    }
}
