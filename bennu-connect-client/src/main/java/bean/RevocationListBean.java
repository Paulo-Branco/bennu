package bean;

import java.util.List;

public class RevocationListBean {
    private List<String> tokenIds;

    public List<String> getTokenIds() {
        return tokenIds;
    }

    public void setTokenIds(List<String> tokenIds) {
        this.tokenIds = tokenIds;
    }
}
