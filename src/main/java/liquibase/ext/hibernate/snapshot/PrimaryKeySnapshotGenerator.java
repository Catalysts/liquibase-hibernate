package liquibase.ext.hibernate.snapshot;

import liquibase.exception.DatabaseException;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.InvalidExampleException;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.PrimaryKey;
import liquibase.structure.core.Table;

public class PrimaryKeySnapshotGenerator extends HibernateSnapshotGenerator {

    private final static Logger log = LogFactory.getLogger();

    public PrimaryKeySnapshotGenerator() {
        super(PrimaryKey.class, new Class[] { Table.class });
    }

    @Override
    protected DatabaseObject snapshotObject(DatabaseObject example, DatabaseSnapshot snapshot) throws DatabaseException, InvalidExampleException {
        return example;
    }

    @Override
    protected void addTo(DatabaseObject foundObject, DatabaseSnapshot snapshot) throws DatabaseException, InvalidExampleException {
        if (!snapshot.getSnapshotControl().shouldInclude(PrimaryKey.class)) {
            return;
        }
        if (foundObject instanceof Table) {
            Table table = (Table) foundObject;
            org.hibernate.mapping.Table hibernateTable = findHibernateTable(table, snapshot);
            org.hibernate.mapping.PrimaryKey hibernatePrimaryKey = hibernateTable.getPrimaryKey();
            if (hibernatePrimaryKey != null) {
                PrimaryKey pk = new PrimaryKey();
                pk.setName(hibernatePrimaryKey.getName());
                pk.setTable(table);
                for (Object hibernateColumn : hibernatePrimaryKey.getColumns()) {
                    pk.getColumnNamesAsList().add(((org.hibernate.mapping.Column) hibernateColumn).getName());
                }
                log.info("Found primary key " + pk.getName());
                table.setPrimaryKey(pk);
            }
        }
    }

}
