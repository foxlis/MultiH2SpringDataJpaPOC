package hello.service;

public interface VersionInfoService {

  String resolvePrimaryDbMetadata();
  String resolveSecondaryDbMetadata();
}
