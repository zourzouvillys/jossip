package com.jive.sip.dummer.txn;

public class BranchGenerator
{

  private final String MAGIC_SELECTOR_COOKIE = "jIv30mn0m";
  private final String prefix;

  public BranchGenerator(final String prefix)
  {
    this.prefix = prefix;
  }

  public String getPrefix()
  {
    return this.MAGIC_SELECTOR_COOKIE + this.prefix;
  }

  public boolean isMine(final String branch)
  {
    return branch.startsWith(this.getPrefix());
  }

}
