public class Fila {
    private QueueNode first;
    private QueueNode top;
    private int size;

    public Fila() {
        this.first = null;
        this.top = null;
        this.size = 0;
    }

    public void enqueue(String e) {
        QueueNode newNode = new QueueNode(e);
        if (isEmpty()) {
            first = newNode;
            top = newNode;
        } else {
            top.next = newNode;
            top = newNode;
        }
        size++;
    }

    public String dequeue() {
        if (isEmpty()) return null;
        String val = first.value;
        first = first.next;
        if (first == null) {
            top = null;
        }
        size--;
        return val;
    }

    public boolean isEmpty() {
        return first == null;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}