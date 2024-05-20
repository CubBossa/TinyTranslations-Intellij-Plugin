package org.intellij.sdk.language.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class KeyRegistry {

  private final Map<String, KeyRegistry> children;

  public KeyRegistry() {
    this(Collections.emptyList());
  }

  public KeyRegistry(Iterable<String> children) {
    this.children = new HashMap<>();
    children.forEach(this::add);
  }

  public Set<String> keySet() {
    return children.keySet();
  }

  public Collection<String> getCompletions(String path) {
    String[] splits = path.split("\\.", -1);
    LinkedList<String> queue = new LinkedList<>(List.of(splits).subList(0, splits.length - 1));
    String s = String.join(".", queue);
    KeyRegistry r = get(queue);
    if (r == null) {
      return Collections.emptyList();
    }
    return r.keySet().stream()
    .map(string -> s.isEmpty() ? string : (s + "." + string))
    .toList();
  }

  public @Nullable KeyRegistry get(String path) {
    if (path.isEmpty()) {
      return get(new LinkedList<>());
    }
    return get(new LinkedList<>(List.of(path.split("\\."))));
  }

  private @Nullable KeyRegistry get(Queue<String> path) {
    if (path.isEmpty()) {
      return this;
    }
    var c = children.get(path.poll());
    if (c != null) {
      return c.get(path);
    }
    return null;
  }

  public void add(String key) {
    if (key.contains(".")) {
      String[] splits = key.split("\\.", 2);
      children.computeIfAbsent(splits[0], s -> new KeyRegistry()).add(splits[1]);
    } else {
      children.put(key, new KeyRegistry());
    }
  }
}
