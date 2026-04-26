public class Player {
    private String nickname;
    private int ranking;

    public Player(String nickname, int ranking) {
        this.nickname = nickname;
        this.ranking = ranking;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
}
