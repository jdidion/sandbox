class Chromosomes(ConstrainedValuesOptionAppender):
    def __init__(self, short_name='c', default=['N']):
        super(Chromosomes, self).__init__(short_name, 'chromosomes', Info('ChromosomeInfo', 
            ['name', 'type', 'size']), default, 'mouse.chromosomes', util.compare_mixed)
    
class Strains(ConstrainedValuesOptionAppender):
    def __init__(self, short_opt='s', default_strains=None, default_file=None):
        super(Strains, self).__init__(short_opt, 'strains', Info('StrainInfo', 
            ['name', 'canonical_name']), default_strains, default_file)