public class Lista {
    private ListaNode head;
    private ListaNode tail;
    private int size;

    public Lista() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void add(Player player) {
        ListaNode newNode = new ListaNode(player);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public void printRanking() {
        if (head == null) {
            System.out.println("O ranking esta vazio.");
            return;
        }
        ListaNode current = head;
        System.out.println("--- RANKING OFICIAL ---");
        while (current != null) {
            System.out.println( "Rank " + current.player.getRanking() +": "+ current.player.getNickname());
            current = current.next;
        }
    }
}