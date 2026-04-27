public class ArvoreBinariaBusca {
    Node root;

    public ArvoreBinariaBusca() {
        this.root = null;
    }

    public int getDepth(String name) {
        return getDepth(root, name, 0);
    }

    private int getDepth(Node current, String name, int depth) {
        if (current == null) return -1;
        if (current.player.getNickname().equalsIgnoreCase(name)) return depth;

        int left = getDepth(current.left, name, depth + 1);
        if (left != -1) return left;

        return getDepth(current.right, name, depth + 1);
    }

    public int getNodeHeight(String name) {
        Node target = searchNode(root, name);
        if (target == null) return -1;
        return calculateHeight(target) - 1;
    }

    private int calculateHeight(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(calculateHeight(node.left), calculateHeight(node.right));
    }

    private Node searchNode(Node current, String name) {
        if (current == null) return null;
        if (current.player.getNickname().equalsIgnoreCase(name)) return current;
        Node leftResult = searchNode(current.left, name);
        if (leftResult != null) return leftResult;
        return searchNode(current.right, name);
    }

    public int getTotalPlayers() {
        return countNodes(root);
    }

    private int countNodes(Node node) {
        if (node == null) return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }

    public Player buscarMaiorRanking() {
        if (root == null) return null;
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.player;
    }

    public boolean existeRanking(int rank) {
        return buscarPorRank(root, rank);
    }

    private boolean buscarPorRank(Node current, int rank) {
        if (current == null) return false;
        if (rank == current.player.getRanking()) return true;
        if (rank < current.player.getRanking()) {
            return buscarPorRank(current.left, rank);
        } else {
            return buscarPorRank(current.right, rank);
        }
    }

    public void insert(Player j) {
        root = insert(root, j);
    }

    private Node insert(Node current, Player j) {
        if (current == null) return new Node(j);
        if (j.getRanking() < current.player.getRanking()) {
            current.left = insert(current.left, j);
        } else {
            current.right = insert(current.right, j);
        }
        return current;
    }

    public boolean search(String name) {
        return searchNode(root, name) != null;
    }

    public Player remove(String name) {
        Node target = searchNode(root, name);
        if (target == null) return null;
        Player removed = target.player;
        int rankAlvo = removed.getRanking();
        root = removePorRank(root, rankAlvo, name);
        return removed;
    }

    private Node removePorRank(Node current, int rank, String name) {
        if (current == null) return null;
        if (rank < current.player.getRanking()) {
            current.left = removePorRank(current.left, rank, name);
        } else if (rank > current.player.getRanking()) {
            current.right = removePorRank(current.right, rank, name);
        } else {
            if (current.player.getNickname().equalsIgnoreCase(name)) {
                if (current.left == null) return current.right;
                if (current.right == null) return current.left;
                Node successor = findMin(current.right);
                current.player = successor.player;
                current.right = removePorRank(current.right, successor.player.getRanking(), successor.player.getNickname());
            }
        }
        return current;
    }

    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public void atualizarRanking(String name, int novoRank) {
        Player p = remove(name);
        if (p != null) {
            p.setRanking(novoRank);
            insert(p);
        }
    }

    public Lista gerarRankingList() {
        Lista rankingList = new Lista();
        inOrderDescending(root, rankingList);
        return rankingList;
    }

    private void inOrderDescending(Node node, Lista list) {
        if (node == null) return;
        inOrderDescending(node.right, list);
        list.add(node.player);
        inOrderDescending(node.left, list);
    }

    public int getHeight() {
        return calculateHeight(root);
    }
}