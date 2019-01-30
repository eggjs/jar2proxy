// Don't modified this file, it's auto created by @ali/jar2proxy
{%- for item in proxyProfile.interfaces -%}
{#- 引入 enum 依赖, 如果第一个属性时枚举类型，并且指向自身，那么这是一个枚举 #}
  {%- set firstField = item.fields[0] %}
  {%- set isEnum = firstField.enumType and firstField.canonicalName == item.canonicalName %}
  {%- if item.canonicalName and isEnum %}
{{ item.commentText | default('') | comment(2) }}
  {%- set canonicalName = item.canonicalName | splitLast('.') | upperFirst %}
import { EnumItem as {{ canonicalName }} } from '*/proxy-enums/{{item.canonicalName.split('.').slice(1, 4).join('-')}}/{{canonicalName}}';
  {%- endif %}
{%- endfor %}

{%- for item in proxyProfile.interfaces -%}
{%- if item.values and item.isEnum %}
enum {{ item.canonicalName | splitLast('.') | upperFirst }} {
  {%- for field in item.values %}
  {{field.key}} = {{ field.value }},
  {%- endfor %}
}
{%- endif %}
{%- endfor %}

{#- 枚举之外的接口描述，依赖放在最上面，所以这里重新循环一遍 #}
{%- for item in proxyProfile.interfaces -%}
{%- set firstField = item.fields[0] %}
{%- set isEnum = firstField.enumType and firstField.canonicalName == item.canonicalName %}
{%- if item.canonicalName and not isEnum and not item.isEnum %}
interface {{ item.canonicalName | splitLast('.') | upperFirst }} {
  {%- for field in item.fields %}
  {%- if not field.isStatic %}
  {{ field.commentText | default('') | comment(2) }}
  {{field.fieldName}}: {{ field.canonicalName | splitLast('.') | upperFirst | getType(field) }};
  {%- endif %}
  {%- endfor %}
}
{%- endif %}
{%- endfor %}

{% for item in proxyProfile.typeList %}
type {{ item.canonicalName | splitLast('.') | upperFirst }} = {%- for field in item.types %}
  {{ field | splitLast('.') | upperFirst | getType(field) }}
  {%- if loop.last != true %}|{% else %};{%- endif %}
  {%- endfor %}
{% endfor %}

{{ proxyProfile.commentText | comment(2) }}
interface {{ interfaceName | splitLast('.') | upperFirst }} {
{%- for item in proxyProfile.methods %}

  {{ item.commentText | comment(2) }}
  {{ item.methodName }}{%- if item.isOverloading %}${{ item.uniqueId }}{%- endif %}(
    {%- for arg in item.params %}
    {{ arg.paramName }}: {{ arg.canonicalName | splitLast('.') | upperFirst | getType(arg) }},
    {%- endfor %}
  ): Promise<{{ item.returnType.canonicalName | splitLast('.') | upperFirst | getType(item.returnType) }}>;

{%- endfor %}
}

declare module '{{ eggFramework }}' {
  export interface IProxy {
    {{proxyName | lowerFirst}}: {{ interfaceName | splitLast('.') | upperFirst }};
  }
}
