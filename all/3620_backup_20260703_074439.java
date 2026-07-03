// 3620. Network Recovery Pathways

import java.util.*;

class Solution {
    public int findMaxPathScore(int[][] edges, boolean[] online, long k) {
        int n = online.length;
        List<int[]>[] graph = new List[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }

        int[] indegree = new int[n];
        long maxCost = 0;

        for (int[] e : edges) {
            int u = e[0], v = e[1], c = e[2];
            graph[u].add(new int[]{v, c});
            indegree[v]++;
            maxCost = Math.max(maxCost, c);
        }

        // Topological order of the DAG
        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) q.offer(i);
        }

        List<Integer> topo = new ArrayList<>(n);
        while (!q.isEmpty()) {
            int u = q.poll();
            topo.add(u);
            for (int[] edge : graph[u]) {
                int v = edge[0];
                indegree[v]--;
                if (indegree[v] == 0) q.offer(v);
            }
        }

        // If no valid path exists even with threshold 0, return -1
        if (!feasible(0L, n, graph, online, topo, k)) {
            return -1;
        }

        // Binary search the maximum possible minimum edge cost
        long lo = 0, hi = maxCost;
        while (lo < hi) {
            long mid = (lo + hi + 1) / 2;
            if (feasible(mid, n, graph, online, topo, k)) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }

        return (int) lo;
    }

    private boolean feasible(
        long x,
        int n,
        List<int[]>[] graph,
        boolean[] online,
        List<Integer> topo,
        long k
    ) {
        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        dist[0] = 0;

        for (int u : topo) {
            if (dist[u] >= INF || !online[u]) continue;

            for (int[] edge : graph[u]) {
                int v = edge[0];
                int cost = edge[1];

                if (!online[v] || cost < x) continue;

                long nd = dist[u] + cost;
                if (nd < dist[v]) {
                    dist[v] = nd;
                }
            }
        }

        return dist[n - 1] <= k;
    }
}
