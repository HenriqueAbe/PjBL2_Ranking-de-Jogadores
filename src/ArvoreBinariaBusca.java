public class ArvoreBinariaBusca {
    Node root;

    public ArvoreBinariaBusca() {
        this.root = null;
    }

    public void insert(Player j) {
        root = insert(root, j);
    }

    public boolean search(String name) {
        return search(root, name) != null;
    }

    public Player remove(String name) {
        Node target = search(root, name);
        if (target == null) return null;
        Player removed = target.player;
        root = remove(root, name);
        return removed;
    }

    private Node insert(Node current, Player j) {
        if (current == null) {
            return new Node(j);
        }
        if (j.getRanking() < current.player.getRanking()) {
            current.left = insert(current.left, j);
        } else if (j.getRanking() > current.player.getRanking()) {
            current.right = insert(current.right, j);
        }
        return current;
    }

    private Node search(Node current, String name) {
        if (current == null) return null;
        if (current.player.getNickname().equalsIgnoreCase(name)) return current;
        Node leftResult = search(current.left, name);
        if (leftResult != null) return leftResult;
        return search(current.right, name);
    }

    private Node remove(Node current, String name) {
        if (current == null) return null;
        if (current.player.getNickname().equalsIgnoreCase(name)) {
            if (current.left == null && current.right == null) return null;
            if (current.left == null) return current.right;
            if (current.right == null) return current.left;
            Node successor = findMin(current.right);
            current.player = successor.player;
            current.right = remove(current.right, successor.player.getNickname());
            return current;
        }
        current.left = remove(current.left, name);
        current.right = remove(current.right, name);
        return current;
    }

    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public void inOrder() {
        inOrder(root);
    }

    private void inOrder(Node node) {
        if (node == null) return;
        inOrder(node.left);
        System.out.println(node.player.getNickname() + " - " + node.player.getRanking());
        inOrder(node.right);
    }

    public int getHeight() {
        return getHeight(root);
    }

    private int getHeight(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }
}