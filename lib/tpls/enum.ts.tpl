/**
 * Don't modified this file, it's auto created by jar2proxy
 * @see {{ canonicalName }}
 */

{{ commentText | comment }}
{% set hasName,hasOrdinal = false %}
interface EnumItem {
  {%- for fieldDesc in fields %}
    {%- if not fieldDesc.enumValue %}
  {{ fieldDesc.fieldName }}: {{ fieldDesc.canonicalName | splitLast('.') | upperFirst | getType(fieldDesc, true) }};
    {%- endif %}
    {%- if not hasName and fieldDesc.fieldName == 'name' %}
      {% set hasName = true %}
    {%- endif %}
    {%- if not hasOrdinal and fieldDesc.fieldName == 'ordinal' %}
      {% set hasOrdinal = true %}
    {%- endif %}
  {%- endfor %}
  {%- if not hasName %}
  name: string;
  {%- endif %}
  {%- if not hasOrdinal %}
  ordinal: number;
  {%- endif %}

  eql(item: EnumItem): boolean;
}

interface EnumClass {
  {%- for fieldDesc in fields %}
    {%- if fieldDesc.enumValue and fieldDesc.fieldName != 'enums' %}
  {{ fieldDesc.commentText | comment(4) }}
  {{ fieldDesc.fieldName }}: EnumItem;
    {%- endif %}
  {%- endfor %}

  enums: EnumItem[];

  getBy<K extends Exclude<keyof EnumItem, 'eql'>, U extends EnumItem[K]>(name: K, val: U): EnumItem;

  getByCode<K extends 'code', U extends EnumItem[Extract<K, keyof EnumItem>]>(code: U): EnumItem;
}

type {{ enumName }} = EnumClass;

export { EnumClass, EnumItem };

export default {{ enumName }};

