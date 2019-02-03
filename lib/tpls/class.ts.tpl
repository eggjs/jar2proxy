// Don't modified this file, it's auto created by jar2proxy

{%- for dep in dependencies -%}
{#- 引入依赖 #}
{{ dep | getImport(class.canonicalName) }}
{%- endfor %}

export default interface {{ class.canonicalName | splitLast('.') | upperFirst }} {
  {%- for field in class.fields %}
  {%- if not field.isStatic and not field.isTransient %}
  {{ field.commentText | default('') | comment(2) }}
  {{field.fieldName}}: {{ field.canonicalName | splitLast('.') | upperFirst | getType(field) }};
  {%- endif %}
  {%- endfor %}
}
