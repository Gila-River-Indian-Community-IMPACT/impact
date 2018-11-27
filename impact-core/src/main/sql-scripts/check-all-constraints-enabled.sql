-- print the enabled/disabled status of all constraints
select 
     [Table]     = o2.name, 
     [Constraint] = o.name, 
     [Enabled]   = case when ((C.Status & 0x4000)) = 0 then 1 else 0 end
from sys.sysconstraints C
     inner join sys.sysobjects o on  o.id = c.constid -- and o.xtype='F'
     inner join sys.sysobjects o2 on o2.id = o.parent_obj
order by Enabled asc
