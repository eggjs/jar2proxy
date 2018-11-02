'use strict';

const nunjucks = require('nunjucks');
const stringify = require('cassandra-map').stringify;

function getType(str, field, onlyBasis) {
  const generic = field && field.generic;
  switch (str) {
    case 'boolean':
    case 'Boolean':
      return 'boolean';
    case 'String':
      return 'string';
    case 'short':
    case 'Short':
    case 'long':
    case 'Long':
    case 'Int':
    case 'int':
    case 'Integer':
    case 'float':
    case 'Float':
    case 'double':
    case 'Double':
    case 'BigDecimal':
      return 'number';
    case 'Void':
    case 'void':
      return 'void';
    case 'Date':
      return 'Date';
    case 'Map':
    case 'HashMap':
    case 'Properties':
    case 'Currency':
      if (Array.isArray(generic) && generic.length === 2) {
        const key = getType(splitLast(generic[0].type, '.'), generic[0], onlyBasis);
        const val = getType(splitLast(generic[1].type, '.'), generic[1], onlyBasis);
        return `{ [key: ${key === 'number' ? key : 'string'}]: ${val} }`;
      }
      return 'Object';
    case 'List':
    case 'ArrayList':
    case 'Collection':
      if (generic && generic.length && generic[0].type) {
        const val = getType(splitLast(generic[0].type, '.'), generic[0], onlyBasis);
        return `${val}[]`;
      }
      return 'any[]';
    case 'Set':
      if (generic && generic.length && generic[0].type) {
        const val = getType(splitLast(generic[0].type, '.'), generic[0], onlyBasis);
        return `Set<${val}>`;
      }
      return 'Set<any>';
    default:
      // T, K, V generic type
      return onlyBasis === true
        ? 'any'
        : str.length === 1 ? 'any' : str;
  }
}

function splitLast(str, sep) {
  if (!str || !str.split) {
    return '';
  }

  const items = str.split(sep);
  return items[items.length - 1];
}
const filters = {
  stringify,
  toURL(val) {
    return val.replace(/\./g, '/');
  },
  comment(text, indent) {
    text = (text || '').trim();

    if (!text) {
      return '';
    }

    indent = indent || 0; // spaces of indent
    indent = new Array(indent + 2).join(' '); // indent string

    return '/**\n' + indent +
      '* ' + text.replace(/\n/g, '\n' + indent + '*') + '\n' + indent +
      '*/';
  },
  antx(val) {
    val = val || '';
    const match = val.match(/^\${(.*)}$/);
    if (match && match[1]) {
      return 'app.config[\'' + match[1] + '\']';
    }
    return '\'' + val + '\'';
  },

  formatParams(str) {
    return (str || '').trim().split(',').filter(function(s) {
      return !!s.trim();
    })
      .map(s => s.trim())
      .join(', ');
  },

  // return first param of arguments
  firstParam(str) {
    return (str || '').trim().split(',').filter(function(s) {
      return !!s.trim();
    })
      .map(s => s.trim())[0];
  },

  arrayParam(str) {
    return (str || '').trim().split(',').filter(function(s) {
      return !!s.trim();
    })
      .map(s => s.trim());
  },

  // split and return the last element
  splitLast,

  upperFirst(str) {
    return str[0].toUpperCase() + str.substring(1);
  },

  lowerFirst(str) {
    return str[0].toLowerCase() + str.substring(1);
  },

  getType(str, field, onlyBasis) {
    const type = getType(str, field, onlyBasis);
    let subfix = '';
    if (field.arrayType) {
      for (let i = 0; i < field.arrayDepth; i++) {
        subfix += '[]';
      }
    }
    return type + subfix;
  },
};


const engine = nunjucks.configure({ autoescape: false, watch: false });
for (const k in filters) {
  engine.addFilter(k, filters[k]);
}

module.exports = engine;
