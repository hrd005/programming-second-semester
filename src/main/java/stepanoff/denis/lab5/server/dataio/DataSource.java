package stepanoff.denis.lab5.server.dataio;

import stepanoff.denis.lab5.common.data.Ticket;
import stepanoff.denis.lab5.server.Collection;

public interface DataSource {

    Collection readAll();

    void writeAll(Collection collection);

    Ticket commitNew(Ticket ticket);

    boolean isCommitNewSupported();
}
