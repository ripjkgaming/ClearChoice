import test from 'node:test';
import assert from 'node:assert/strict';
import { createProfile, resolveProfile, normalizePerception, validateBridgeAction } from '../../packages/engine/src/index.js';

test('app/screen entries without overrides inherit global pause and rule settings', () => {
  const p = createProfile({ global:{ paused:true, rules:{ clutter:false } }, apps:{ shop:{} } });
  const r = resolveProfile(p,'shop','x');
  assert.equal(r.paused, true);
  assert.equal(r.rules.clutter, false);
  assert.equal(r.rules['hidden-fees'], true);
});

test('bridge rejects clickable controls whose text was redacted as sensitive', () => {
  const p = { sourceRevision:'r1', elements:[{ id:'b', type:'button', text:'Enter credit card number', visible:true, enabled:true, clickable:true, actions:['click'] }] };
  assert.equal(normalizePerception(p).elements[0].text, '[sensitive content omitted]');
  assert.throws(() => validateBridgeAction(p,{ actionId:'a', sourceId:'b', sourceRevision:'r1', action:'click' }), /sensitive/);
});
