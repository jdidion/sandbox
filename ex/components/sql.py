import logging
import re

class SQL(Component):
    __slots__ = ['statements', 'options']
    
    def __init__(self, db, *args, sql_file=None, options=None, close_connection=True, terminator=';'):
        Component.__init__(self, options)
        self.db = db
        self.statements = args
        self.close_connection = close_connection
        # Add command line options for SQL statements and statement files if no
        # statements are given as arguments
        if sql_file is not None:
            sql = io_util.safe_read_file(sql_file)
            if sql is None or len(sql) == 0:
                logging.warn("No SQL statements found in %s", sql_file)
            else:
                self.statements.append(sql.split(terminator))
        elif len(args) == 0:
            self.options.append(ValuesAppender('sql', 's', comparator=None, 
                parser_options={'terminator': terminator}))
    
    def process_options(self, options):
        if 'sql' in dir(options):
            self.statements.append(options.sql)
        # Clean statements
        # TODO: do something with comments?
        self.statements = [_clean_statement(s)[0] for s in self.statements]
        
    def __call__(self, options):
        db = self.db
        if not db.is_open():
            db.open()

        # TODO: validation
        # TODO: handle results
        for s in self.statements:
            # interpolate
            s = s % options
            # execute
            db.execute(s)
        
        if self.close_connection:
            db.close()
    
    def _clean_statement(stmt):
        '''
        Remove comments and leading/trailing whitespace. Returns a tuple of (statement,comments).
        '''
        m = re.match('((?:^#[^\n]*\n)*)(.+)', stmt.strip(), re.DOTALL|re.MULTILINE)
        return (m.group(2).strip(), m.group(1).strip())