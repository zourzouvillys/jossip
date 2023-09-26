import { PerformanceObserver, performance } from 'node:perf_hooks';
import SipClient from './client.mjs';
import { toSIP } from './message.mjs';


const obs = new PerformanceObserver((items) => {
  console.log(items.getEntries()[0].duration);
  // performance.clearMarks();
});

obs.observe({ entryTypes: ["measure"], buffer: true })


async function main() {

  const server = 'localhost:8881';
  const target = process.argv[2];

  const client = new SipClient(server);

  for (let i = 0; i < 3; ++i) {
    performance.mark('A');
    const answer = await client.exchange(target, {
      method: 'REGISTER',
      uri: 'sip:test',
      headers: [
        { name: 'via', values: ['SIP/2.0/UDP invalid;branch=xyz'] }
      ]
    });
    console.log(toSIP(answer.frame))
    // await new Promise((resolve) => setTimeout(resolve, 1000));
    performance.mark('B');
    performance.measure('A to B', 'A', 'B');
  }

}

main().then(console.log);