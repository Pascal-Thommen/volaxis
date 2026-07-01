package edu.upa.tie;

import edu.upa.tie.model.Usuario;

public class Session {
    private static Usuario currentUser;

    public static void set(Usuario user) { currentUser = user; }
    public static Usuario get() { return currentUser; }
    public static void clear() { currentUser = null; }
    public static boolean isAdmin() { return currentUser != null && currentUser.isAdmin(); }
}
